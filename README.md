# opi-lab3

> Вариант: 16521

в `build.gradle` были добавлены следующие таски:

- `myCompile` - компиляция исходных кодов проекта
- `myBuild` - компиляция исходных кодов проекта и их упаковка в исполняемый jar-архив
- `myWar` - сборка war-файла, который можно запустить из wildfly
- `myClean` - удаление скомпилированных классов проекта и всех временных файлов (если они есть)
- `myCompileTest` - компиляция тестовых файлов
- `myTest` - запуск junit-тестов проекта
- `music` - воспроизведение музыки по завершению сборки
- `myEnv` - осуществляет сборку и запуск программы в альтернативных окружениях; окружение задается версией java и набором аргументов виртуальной машины в файле параметров

## Граф зависимостей задач
```
*   myCompileTest --+
                    |
                    myCompile ------+--> myWar --> myBuild --> music
                                    |
                                    +--> myEnv
*   myTest --> myCompileTest
*   myClean
```

## Запуск
```bash
./gradlew myBuild
```
```bash
java -Dwildfly.home=/home/maxkarn/Desktop/wildfly/wildfly-39.0.1.Final \
     -Dwildfly.admin.name=admin \
     -Dwildfly.admin.password=admin \
     -Dwildfly.jdbc.driver=/home/maxkarn/Desktop/labs/opi/lab3/web_lab3/psql_jdbc_driver/postgresql-42.7.8.jar \
     -Dwildfly.wait=45 \
     -jar build/libs/web_lab3.jar
```