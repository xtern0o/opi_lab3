package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Launcher {
    public static void main(String[] args) throws Exception {
        String wildflyHome = System.getProperty("wildfly.home");

        if (wildflyHome == null) {
            System.err.println("Launcher: укажи -Dwildfly.home=<путь до wildfly директории (там есть jboss-modules.jar)>");
            System.exit(1);
        }

        int waitSeconds = Integer.parseInt(System.getProperty("wildfly.wait", "30"));

        // опциональные параметры админа (если не указаны - пропускаем создание)
        String adminName = System.getProperty("wildfly.admin.name");
        String adminPassword = System.getProperty("wildfly.admin.password");

        // jdbc psql driver path
        String jdbcDriverPath = System.getProperty("wildfly.jdbc.driver");

        Path wildflyPath = Paths.get(wildflyHome);
        Path deploymentDir = wildflyPath.resolve("standalone/deployments");

        // извлекаем war из ресурсов jar
        if (!Files.isDirectory(deploymentDir)) {
            System.err.println("Launcher: директория deployments не найдена: " + deploymentDir);
        }

        // создаем юзерадмина
        if (adminName != null && adminPassword != null) {
            createAdminUser(wildflyPath, adminName, adminPassword);
        } else {
            System.out.println("Launcher: wildfly.admin.name/password не указаны, пропускаем создание админа");
        }

        if (jdbcDriverPath != null) {
            deployJdbcDriver(Paths.get(jdbcDriverPath), deploymentDir);
        } else {
            System.out.println("Launcher: LLL driver skip... (wildfly.jdbc.driver не указан)");
        }

        // извлекаем ebmedded war из jar-ника
        Path warFile = extractEmbeddedWar();
        if (warFile == null) {
            System.out.println("ошибочка вышла");
            return;
        }
        System.out.println("WAR извлечен во временный файл: " + warFile);

        // запускаем дикую муху
        startWildfly(wildflyPath);
        System.out.println("Launcher: WILDFLY ЗАПУЩЕН!");

        waitForPort("localhost", 8080, waitSeconds);
        System.out.println("Launcher: WILDFLY ГОТОВ ПРИНИМАТЬ ЗАПРОСЫ!!");

        // деплоим
        Path dest = deploymentDir.resolve("opi_lab3.war");
        Files.copy(warFile, dest, StandardCopyOption.REPLACE_EXISTING);
        Files.write(deploymentDir.resolve("opi_lab3.war.dodeploy"), new byte[0]);
        System.out.println("[Launcher] WAR задеплоен: " + dest);

        System.out.println("Launcher: ВСЕ СЮДА -> http://localhost:8080/opi_lab3/");

    }

    /**
     * Статический метод для извлечения war-файла для деплоя из jar-ника
     * (он включен в classpath)
     * @return Path-объект с war
     */
    private static Path extractEmbeddedWar() {
        try (InputStream in = Launcher.class.getResourceAsStream("/embedded.war")) {
            if (in == null) throw new FileNotFoundException("Launcher: embedded.war не найден внутри нашего ДЖАР");
            Path tmp = Files.createTempFile("wildfly-deploy-", ".war");
            tmp.toFile().deleteOnExit(); // чтобы все обрубилось когда мы выходим
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            return tmp;
        } catch (IOException ioException) {
            System.out.println("Launcher: ошибка при извлечении war внутри jar: " + ioException);
            return null;
        }
    }

    /**
     * Стартуем процесс дикой мухи
     * stdout и stderr перенаправляем в файлы
     * @param wildflyPath (Path) путь до wildfly
     */
    private static void startWildfly(Path wildflyPath) {
        try {
            Path standaloneScript = wildflyPath.resolve("bin/standalone.sh");
            new ProcessBuilder(standaloneScript.toString())
                    .directory(wildflyPath.toFile())
                    .redirectOutput(new File("wildfly-stdout.log"))
                    .redirectError(new File("wildfly-stderr.log"))
                    .start();
        } catch (IOException ioException) {
            System.out.println("Launcher: ошибка при запуске wildfly: " + ioException);
        }
    }

    /**
     * Статический метод, который кидает сокет-запросы на wildfly-хост в ожидании его открытия
     * Запросы кидаются раз в секунду до того момента, пока время не превысит maxSec
     * @param host хост
     * @param port порт
     * @param maxSec граница ожидания
     * @throws Exception эксепшин)
     */
    private static void waitForPort(String host, int port, int maxSec) throws Exception {
        long deadline = System.currentTimeMillis() + maxSec * 1000L;
        System.out.println("Ожидаем пока откроется порт...");
        while (System.currentTimeMillis() < deadline) {
            try (Socket ignored = new Socket(host, port)) {
                System.out.println("ОТКРЫЛСЯ!!!!");
                return;
            } catch (IOException e) {
                System.out.println(".");
                Thread.sleep(1000);
            }
        }
        throw new RuntimeException("WildFly не поднялся за " + maxSec + " секунд :((( :((((");
    }

    /**
     * Создание админ-юзера с логином да паролем
     * @param wildflyPath путь до дикой мухи
     * @param name имя админа
     * @param password пароль админа
     * @throws Exception экспешин если что то не так
     */
    private static void createAdminUser(Path wildflyPath, String name, String password) throws Exception {
        System.out.println("Launcher: создаем админа по имени " + name + " админович");

        String script = wildflyPath.resolve("bin/add-user.sh").toString();

        Process process = new ProcessBuilder(script, "-u", name, "-p", password, "-s", "-r", "ManagementRealm")
                .directory(wildflyPath.toFile())
                .inheritIO() // показываем вывод чтобы видеть ошибки
                .start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("add-user.sh завершился с кодом НЕ 0: " + exitCode);
        }

        System.out.println("Launcher: Админ создан!!");
    }

    /**
     * Деплоить jdbc драйвер на деплой
     * @param driverFile путь до драйвера
     * @param deploymentsDir куда деплоим
     * @throws IOException если чет не так
     */
    private static void deployJdbcDriver(Path driverFile, Path deploymentsDir) throws IOException {
        if (!Files.exists(driverFile)) {
            throw new FileNotFoundException("jdbc драйвер не найден: " + driverFile);
        }

        Path dest = deploymentsDir.resolve(driverFile.getFileName());
        Files.copy(driverFile, dest, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Launcher: jdbc скопирован (задеплоен): " + dest);
    }

}
