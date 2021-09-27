# Van Gogh's Blogging Project

## Introduction
This blogging project enables Van Gogh's fans to comment on his masterpieces.
The main page of the blog website has a Dashboard. It consists of selected 
paintings originated from the latest posts. Each painting image on dashboard
can be clicked to see the post details.

Users can view Van Gogh paintings publicly without login. A registered user 
can create a new post with one of Van Gogh's painting, the painting name, and 
its description. Any registered users can comment on the post. Comments in the 
posts can be viewed publicly. Like a post, comment can only be created by any 
registered users. Unlike a post, a comment can be deleted by its owner only.

The integration app scales well with high availability and fault tolerant 
considerations in mind (see the flow below).  
```
User --- Hosted Domain (Google) --- ReactJS https (AWS Amplify) --- Route 53
--- AWS App Load Balancer --- AWS EC2s --- SpringBoot REST Https --- EC2 DB Docker
```

To get the essence of the simplicity implementation,
the author intentionally does not go into deep details to setup AWS components
and SSL certificate.


## Deploying Spring boot Backend jar to AWS EBS Service
Follow steps below to build the blog backend code and deploy its jar.
```
1. $ mvn clean install
2. Use your AWS account to subscribe the EBS (Elastic Beanstalk) service.  
3. Choose "Java" as the platform.
4. Upload your local jar file to deploy to EBS.
5. Add a property with name SERVER_PORT with a value 5000 to make the blog app
listen on this port.
6. Test your instance with Postman or a browser.
http://<...>.elasticbeanstalk.com/content/post/1
```


## Persisting Posts and Comments
MySQL DB is used to persist users' posts and comments. The DB runs in a docker 
environment on an AWS EC2 instance. 

Follow the steps below to install and setup MySQL in your own docker environment, 

* install and run MySQL
```bash
$ docker run -p 3306:3306 -p 33060:33060 --name=mysql57 -d mysql/mysql-server:5.7
```

* grab the default password:
```bash
$ docker logs mysql57 2>&1 | grep GENERATED
```

* Connect using mysql client directly to the mysqld in docker:
```bash
$ docker exec -it mysql57 mysql -uroot -p
```

* Change root password
```bash
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY '<your_own_password>';
```

* Run SQL
```bash
mysql> update mysql.user set host = '%' where user='root';
```

* Exit
```bash
mysql> quit
```

* Restart docker
```bash
% docker restart mysql57
```


* Make the backend code points to other MySQL instance, 
```bash
spring.datasource.url=jdbc:mysql://<hostname>:<port>/<database_name>
spring.datasource.username=<username>
spring.datasource.password=<password>
```

## Cleanup
Run the command below to remove all build artifacts. This action deletes the
folder target and everything under it.
```bash
$ mvn clean
```
If you want to build target, setup database schema (per application.properties 
configuration), and produce jar file for deployment after cleanup, run this command
```bash
$ mvn clean install
```

## License

[GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)