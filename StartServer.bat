@echo off

rem Запустим пока бесконечный цикл

echo Старт сервера очереди
rem java -cp dist/QSystem.jar;plugins/ZoneboardPlugin.jar;D:/Apertum/QSkySenderPlugin/dist/QSkySenderPlugin.jar;pugins/ClientboardRS485Plugin.jar ru.apertum.qsystem.server.QServer debug
java -cp dist/QSystem.jar ru.apertum.qsystem.server.QServer -http 8081 -debug 

echo Сервер очереди прекратил работу

pause

