sudo -i
sudo  apt install ffmpeg libmariadb3 libpq5 libmicrohttpd12

sudo  apt install python2 python-dev-is-python2

sudo  apt install libssl-dev libcurl4-openssl-dev libjpeg-dev zlib1g-dev
#Download motion eyeos
wget https://github.com/Motion-Project/motion/releases/download/release-4.3.2/pi_buster_motion_4.3.2-1_armhf.deb
#Run dpkg Command
dpkg -i pi_buster_motion_4.3.2-1_armhf.deb 
#Install PYPA:
curl https://bootstrap.pypa.io/pip/2.7/get-pip.py --output get-pip.py
python2 get-pip.py
#Install MotionEye:
pip2 install motioneye
#Setting Up The Directory:
mkdir -p /etc/motioneye
cp /usr/local/share/motioneye/extra/motioneye.conf.sample /etc/motioneye/motioneye.conf
#Preparing The Media Directory:
mkdir -p /var/lib/motioneye
#Add An Init Script, Configure It To Run At Start And Run MotionEye:
cp /usr/local/share/motioneye/extra/motioneye.systemd-unit-local /etc/systemd/system/motioneye.service
systemctl daemon-reload
systemctl enable motioneye
systemctl start motioneye
#To Upgrade:
pip install motioneye --upgrade
systemctl restart motioneye
exit