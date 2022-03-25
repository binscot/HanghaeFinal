#!/bin/bash

#현재 서비스하고 있는 WAS 포트 번호 읽어오기
#CURRENT_PORT=$(cat /etc/nginx/conf.d/service-url.inc | grep -Po '[0-9]+' | tail -1)
CURRENT_PORT=$(cat /home/ubuntu/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Current port of running WAS is ${CURRENT_PORT}."

# 현재 포트가 8081이면 새로운 WAS 띄울 타켓은 8082, 혹은 그 반대
if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081
else
  echo "> No WAS is connected to nginx"
fi

TARGET_PID=$(sudo lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')
CURRENT_PID=$(sudo lsof -Fp -i TCP:${CURRENT_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

sudo lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+' | cat >/home/ubuntu/app/deploy/file

echo "CURRENT_PID -- ${CURRENT_PID}"  
echo "TARGET_PID -- ${TARGET_PID}"  

# 만약 타겟포트에도 WAS 떠 있다면 kill하고 새롭게 띄우기
echo "${TARGET_PID}" 
if [ ! -z $TARGET_PID ]; then
  sudo kill ${TARGET_PID}
  echo "> Kill WAS running at ${TARGET_PORT}."
fi

#마지막&는 프로세스가 백그라운드로 실행되도록 해준다.
nohup java -jar -Dserver.port=${TARGET_PORT} /home/ubuntu/wewrite/build/libs/user-0.0.1-SNAPSHOT.jar > /home/ubuntu/nohup.out 2>&1 &
echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0
