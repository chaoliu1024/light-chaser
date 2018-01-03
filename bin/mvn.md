mvn clean package -P test -Dmaven.test.skip=true

mvn clean package -P test -X -Dmaven.test.skip=true

非python运行
windows
java -cp conf;light-chaser.jar group.chaoliu.lightchaser.core.daemon.photon.Photon

linux
java -cp conf:light-chaser.jar group.chaoliu.lightchaser.core.daemon.photon.Photon

nohup java -cp conf:light-chaser.jar group.chaoliu.lightchaser.core.daemon.LocalDaemon ota ctrip > /dev/null 2>&1 &

nohup java -cp conf:light-chaser.jar group.chaoliu.lightchaser.core.daemon.photon.Radiator &