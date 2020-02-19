# BKChat

A Peer-to-peer chat application designed and developed by group **C&Q** from _Ho Chi Minh city University of Technology (HCMUT)_

![Demo-intro](https://github.com/vuong-cuong-phoenix/BKChat/blob/master/images/Demo-intro.png)
![Demo-main](https://github.com/vuong-cuong-phoenix/BKChat/blob/master/images/Demo-main.png)

This is a product for _Assignment 1 (2019-2020)_ of _Computer Network (CO3003)_

## Requirements

This repo is the source-code, not the complied once, so you need to install some requirements first in order to run correctly.

_Recommend: You should use IntelliJ IDEA to compile and run this application for the best experiment._

1. Java Development Kit 8 (include JavaFX 8)
2. [MariaDB](https://mariadb.org/)
3. Libraries for Java 8:

    - [Apache commons lang 3](https://commons.apache.org/lang/download_lang.cgi)
    - [FontAwesomeFX 8](https://bitbucket.org/Jerady/fontawesomefx/downloads/)

## Compile and run

Follow these steps to compile and run the application correctly:

1. Initialize Database:

    Run all MariaDB's queries in file [./src/BKChat.sql](https://github.com/vuong-cuong-phoenix/BKChat/blob/master/src/BKChat.sql)

2. Edit Connector:

    Edit file [./src/app/controllers/DBController.java](https://github.com/vuong-cuong-phoenix/BKChat/blob/master/src/app/controllers/DBController.java):

    Change `db_username` and `db_password` as the user's name and password of MariaDB that you was using to run all queries above.

3. Run Server:

    Compile and run file [./src/app/socket/ServerMaster.java](https://github.com/vuong-cuong-phoenix/BKChat/blob/master/src/app/socket/ServerMaster.java) first.

4. Run Application:

    Compile and run file [./src/app/socket/Main.java](https://github.com/vuong-cuong-phoenix/BKChat/blob/master/src/app/Main.java) and enjoy!

## Known issues

Any known issues will be placed here with solution. You should also help us detecting bugs and errors to fix them as soon as possible!
