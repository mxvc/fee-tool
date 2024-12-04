@echo off
cd /d %~dp0




echo check jdk install ...
if not exist jdk (
    if not exist jdk-21_windows-x64_bin.zip (
        wget --no-check-certificate https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.zip
    )

   7z x -y jdk-21_windows-x64_bin.zip -o./
   del *.zip

   echo rename jdk dir name ....
   dir /b | find "jdk-21." >  jdk_dir_name.txt
   for /f "delims=[" %%i in (jdk_dir_name.txt) do (
       echo %%i
       ren %%i jdk
   )
   del jdk_dir_name.txt
)

chcp 65001 && jdk\bin\java -jar fee-tool.jar

pause
