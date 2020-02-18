CREATE DATABASE IF NOT EXISTS BKChat;

USE BKChat;

-- user (id, userName, userPassword, userNickname, user_isAdmin)
-- userFriend (user1, user2)

DROP TABLE IF EXISTS tbl_userFriend;
DROP TABLE IF EXISTS tbl_user;

CREATE TABLE IF NOT EXISTS tbl_user (
    userName VARCHAR(64) NOT NULL,
    userPassword NVARCHAR(256) NOT NULL,
    userNickname NVARCHAR(128) NOT NULL DEFAULT 'Default Nickname',
    userAvatar NVARCHAR(256) NOT NULL DEFAULT 'avatar-default.jpg',
    
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS tbl_userFriend (
	user1 VARCHAR(64) NOT NULL,
    user2 VARCHAR(64) NOT NULL,
    
    FOREIGN KEY (user1) REFERENCES BKChat.tbl_user(userName),
    FOREIGN KEY (user2) REFERENCES BKChat.tbl_user(userName)
);
-- ADD SAMPLE ADMIN --
INSERT INTO tbl_user (userName, userPassword, userNickname, userAvatar) VALUES("admin", "bkcadmin", "Admin", "avatar-default.jpg");
-- ADD SOME SAMPLE USERS --
INSERT INTO tbl_user (userName, userPassword, userNickname, userAvatar) VALUES("cuong", "123456", "Vương Cường", "avatar1.jpg");
INSERT INTO tbl_user (userName, userPassword, userNickname, userAvatar) VALUES("quan", "123", "Quân Bùi", "avatar2.jpg");
INSERT INTO tbl_user (userName, userPassword, userNickname, userAvatar) VALUES("dung", "123", "Dũng", "avatar3.jpg");

-- User Stored Procedure (USP) for Signing up... --
DELIMITER $$
DROP PROCEDURE IF EXISTS USP_Signup $$
CREATE PROCEDURE USP_Signup(IN user_Name VARCHAR(64), IN user_Password NVARCHAR(64), IN user_Nickname NVARCHAR(128))
BEGIN 
	DECLARE counted INT;
    
	SET counted = (SELECT COUNT(*) FROM tbl_user WHERE userName = user_Name);
    
    IF counted = 0
    THEN INSERT INTO BKChat.tbl_user (userName, userPassword, userNickname) 
			VALUES(user_Name, user_Password, user_Nickname);
    END IF;
END; $$
DELIMITER ;

-- User Stored Procedure (USP) for Add Friend... --
DELIMITER $$
DROP PROCEDURE IF EXISTS USP_AddFriend $$
CREATE PROCEDURE USP_AddFriend(IN user1_name VARCHAR(64), IN user2_name VARCHAR(64))
BEGIN 
	DECLARE countedExists INT;
    
    SET countedExists = (SELECT COUNT(*) FROM BKChat.tbl_user WHERE userName = user1_name OR userName = user2_name);
    
    IF countedExists = 2
    THEN 
		BEGIN
			DECLARE countedFriend INT;
			
			SET countedFriend = (SELECT COUNT(*) FROM BKChat.tbl_userFriend WHERE (
				(user1 = user1_name AND user2 = user2_name) OR
				(user1 = user2_name AND user2 = user1_name)
			));
			
			IF countedFriend = 0
			THEN
				BEGIN
					INSERT INTO BKChat.tbl_userFriend (user1, user2) VALUES (user1_name, user2_name);
					INSERT INTO BKChat.tbl_userFriend (user1, user2) VALUES (user2_name, user1_name);
				END;
			END IF;
		END;
	END IF;
END; $$
DELIMITER ;

CALL USP_AddFriend("admin", "cuong");
CALL USP_AddFriend("cuong", "dung");
CALL USP_AddFriend("quan", "dung");

SELECT * FROM BKChat.tbl_user;
SELECT * FROM BKChat.tbl_userFriend;

