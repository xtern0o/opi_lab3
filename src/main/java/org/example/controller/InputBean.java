package org.example.controller;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.PointDto;
import org.example.entity.PointEntity;
import org.example.managers.PointsBean;
import org.example.services.WeatherService;
import org.example.utils.exceptions.APIException;
import org.example.utils.exceptions.ValidationError;
import org.example.utils.validators.PointValidator;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Бин, отвечающий за данные в форме каждого пользователя
 */
@Data
@Named("inputBean")
@ViewScoped
public class InputBean implements Serializable {
    @Inject
    PointsBean pointsBean;
    @Inject
    PointValidator pointValidator;
    @Inject
    WeatherService weatherService;

    private Float x;
    private Float y;
    private List<String> selectedRValues;
    private Float temperature;

    private Float maxR;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String ip;

    @PostConstruct
    public void init() {
        selectedRValues = new ArrayList<>();
        setCurrentIp();
        refreshTemperature();
    }

    /**
     * Очистка полей ввода
     */
    public void clear() {
        x = null;
        y = null;
        selectedRValues.clear();
    }

    /**
     * Метод, который вызывает ajax-событие изменения радиуса
     */
    public void onRChange() {
        List<Float> floatSelectedR = selectedRValues.stream().map(Float::parseFloat).toList();
        this.maxR = floatSelectedR.stream().max(Float::compare).orElse(null);

        String rJson = new Gson().toJson(selectedRValues);

        PrimeFaces.current().ajax().addCallbackParam("selectedRJson", rJson);
    }

    /**
     * Отправка формы, проверка факта попадания точек и обновление кэша
     */
    public void check() {
        if (selectedRValues.isEmpty()) throw new ValidatorException(new FacesMessage("Ошибка валидации", "Валидация провалилась!"));

        refreshTemperature();
        PrimeFaces.current().ajax().addCallbackParam("refreshedTemp", temperature);

        List<PointDto> pointDTOs = new CopyOnWriteArrayList<>();
        selectedRValues.forEach((r) -> pointDTOs.add(new PointDto(x, y, Float.parseFloat(r), this.temperature)));
        addPoints(pointDTOs);
    }

    /**
     * Инкапсулированный метод для добавления точек через контроллер
     * @param pointDTOs массив из дто
     */
    private void addPoints(List<PointDto> pointDTOs) {
        for (int i = 0; i < pointDTOs.size(); i++) {
            PointDto currentPointDTO = pointDTOs.get(i);
            currentPointDTO.setHit(pointValidator.checkArea(currentPointDTO));
            pointDTOs.set(i, currentPointDTO);
        }

        List<PointEntity> pointEntities = new CopyOnWriteArrayList<>();
        pointDTOs.forEach(pointDTO -> pointEntities.add(new PointEntity(pointDTO)));
        try {
            pointsBean.addAll(pointEntities);
        } catch (ValidationError validationError) {
            FacesMessage facesMessage = new FacesMessage("Ошибка валидации", validationError.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        } finally {
            String pointsJsonRaw = new Gson().toJson(pointDTOs);
            PrimeFaces.current().ajax().addCallbackParam("pointsJsonRaw", pointsJsonRaw);
        }
    }

    public void addPointViaCanvas() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String x = facesContext.getExternalContext().getRequestParameterMap().get("x");
        String y = facesContext.getExternalContext().getRequestParameterMap().get("y");
        List<String> rList = (List<String>) new Gson().fromJson(facesContext.getExternalContext().getRequestParameterMap().get("rList"), List.class);

        List<PointDto> pointDTOs = new CopyOnWriteArrayList<>();
        rList.forEach(
                (r) -> pointDTOs.add(
                        new PointDto(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(r), this.temperature)
                )
        );
        addPoints(pointDTOs);
    }

    /**
     * Получить просто все точки из кэша
     * @return List из DTOшек
     */
    public List<PointDto> getAllPoints() {
        return pointsBean.getAll().stream()
                .map(PointDto::new)
                .toList();
    }

    /**
     * Получить и перевернуть все точки из кэша
     * @return List из DTOшек
     */
    public List<PointDto> getReversedPoints() {
        List<PointDto> reversedList = new ArrayList<>(getAllPoints());
        Collections.reverse(reversedList);
        return reversedList;
    }

    /**
     * Получить все точки в виде Json
     * @return json-массив с точками
     */
    public String getAllPointsAsJson() {
        return new Gson().toJson(getAllPoints());
    }

    /**
     * Получение температуры по айпишнику
     */
    private void refreshTemperature() {
        try {
            this.temperature = weatherService.getTemperatureByIp(this.ip);
        } catch (APIException apiException) {
            FacesMessage apiExceptionMessage = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Ошибка API",
                    apiException.getMessage()
            );
            FacesContext.getCurrentInstance().addMessage(null, apiExceptionMessage);
        }
    }

    /**
     * Метод для получения айпишника пользователя
     */
    private void setCurrentIp() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();

        String ipAdress = httpServletRequest.getHeader("X-FORWARDED-FOR");
        if (ipAdress != null && !ipAdress.isEmpty()) {
            ipAdress = ipAdress.split(",")[0];
        } else {
            ipAdress = httpServletRequest.getRemoteAddr();
        }

        this.ip = ipAdress;
    }

    /**
     * Функция-связка для вывода primeFaces тоастов из js
     */
    public void addFacesMessage() {
        FacesContext context = FacesContext.getCurrentInstance();
        String summary = context.getExternalContext().getRequestParameterMap().get("summary");
        String detail = context.getExternalContext().getRequestParameterMap().get("detail");
        String severityParam = context.getExternalContext().getRequestParameterMap().get("severity");

        FacesMessage.Severity severity = switch (severityParam) {
            case "info" -> FacesMessage.SEVERITY_INFO;
            case "warn" -> FacesMessage.SEVERITY_WARN;
            case "error" -> FacesMessage.SEVERITY_ERROR;
            case "fatal" -> FacesMessage.SEVERITY_FATAL;
            default -> FacesMessage.SEVERITY_INFO;
        };

        context.addMessage(null, new FacesMessage(severity, summary, detail));
    }

}
