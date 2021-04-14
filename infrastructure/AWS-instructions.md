# Setting up AWS
This doc acts as documentation on the process needed to setup the AWS infrastructure needed for Appsisted-Parking.

## Launching a VM
1. Sign in to AWS
2. Launch a virtual machine
3. Select "Ubuntu server 20.04 LST - 64 bit (x86)"
4. Select t2.micro (free tier eligible)
5. Click - Review and Launch
6. Click - Launch
```
<A dialogue will appear asking for auth preferences>
```
7. Select Create a new Key-pair & give it any name
8. Click - "Download Key Pair"
```
<save .pem file, its needed for accessing the intance>
```
9. Click - launch instance
10. Click - View Instances
11. Find the IPv4 - its further right on the instance table
12. Open up git-bash
13. give the pem file permissions to be used
```
chmod 664 <path-to-file>.pem
e.g:  chmod 664 ~/Desktop/key.pem
14 ssh into the machine using the ubuntu user name:
ssh -i <path_to_pem> ubuntu@<public ipv4>
e.g: ssh -i kp1.pem ubuntu@18.135.102.180
```
14. type in "yes" when asked if the key should be added to known hosts

## Installing Cassandra

### Java Install

1. update the linux ubuntu install repo
```
sudo apt-get update
```
2. install open-jdk 8
```sudo apt-get install openjdk-8-jdk
< A prompt will ask a y/n question on where to install >
```
3. input "y"
4. Test java was installed
```
  java -version

  Output:
  ubuntu@ip-172-31-22-151:~$ java -version
  openjdk version "1.8.0_282"
  OpenJDK Runtime Environment (build 1.8.0_282-8u282-b08-0ubuntu1~20.04-b08)
  OpenJDK 64-Bit Server VM (build 25.282-b08, mixed mode)
```

### Cassandra Install

5. Download a cassandra install package for Cassandra v4 using `wget`
```
wget https://mirrors.gethosted.online/apache/cassandra/4.0-beta4/apache-cassandra-4.0-beta4-bin.tar.gz
```
6. unarchive the compressed file
```
tar -xvf
```
7. verify the unzipping
```
  ls

  Output:
  apache-cassandra-4.0-beta4  apache-cassandra-4.0-beta4-bin.tar.gz
```

8. Configure Cassandra to use LESS memory or it will slow down the VM. Do this by editing line 73 of <cassandra_dir>/conf/cassandra-env.sh.
```
  // change from:
  //     MAX_HEAP_SIZE="${max_heap_size_in_mb}M"
  // change to:
  //     MAX_HEAP_SIZE="300M"
```

8. Start cassandra in the background
```
cd ~/apache-cassandra-4.0-beta4/bin
./cassandra
```

9. Start cqlsh to verify

## Install Backend Web-Service

### Build & Upload the Service
1. Build the service - Open IntelliJ
2. Click on the "maven" section
3. Click "package" and then run it (green triangle button)
```
< jar will be built in target/appsisted-parking-0.0.1-SNAPSHOT.jar >
```
4. Upload the jar to aws using scp - open a git-bash shell
5. Run the command:
```
scp -i <path_to_pem> <path_to_jar> ubuntu@<ip_host>:/home/ubuntu
e.g: scp -i ~/Desktop/AWS/kp1.pem appsisted-parking-0.0.1-SNAPSHOT.jar ubuntu@18.132.197.92:/home/ubuntu
```
6. Start the service - ssh into the AWS instance:
```
ssh -i <path_to_pem> ubuntu@<public ipv4>
```
7. Start the jar:
```
java -jar appsisted-parking-0.0.1-SNAPSHOT.jar
```
8. Verify the service works locally:
```
curl localhost:8080/
```

## AWS Config
### Open the ports - Enable traffic out for port 8080
1. Open the AWS Console (web page)
2. Navigate to instances and click on the blue instance ID link
3. Click on security
4. Click on the assigned security group link (e.g: sg-0168ae5e0afff2f14 (launch-wizard-1))
5. Click on edit inbound rules
6. Click "add rule" and input the following: Custom TCP / port range - 8080 / 0.0.0.0/0
7. Click save rules
8. Do the same for outbound rules.
