DROP TABLE IF EXISTS MatchSession;
DROP TABLE IF EXISTS GamePool;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS imageData;

CREATE TABLE Users
(
	userID                          VARCHAR(10) PRIMARY KEY,
	email                           VARCHAR(50),
	password			VARCHAR(15),
	score				FLOAT
);

CREATE TABLE GamePool
(
	userID				VARCHAR(10) PRIMARY KEY,	
	opponentUserID                  VARCHAR(10),
	CONSTRAINT fk_GamePool_userID FOREIGN KEY (userID) REFERENCES Users(userID),
	CONSTRAINT fk_GamePool_opponentUserID FOREIGN KEY (opponentUserID) REFERENCES Users(userID)
);

CREATE TABLE MatchSession
(
	matchSessionID			INT(10) NOT NULL AUTO_INCREMENT,
        matchID                         VARCHAR(10),
	userID				VARCHAR(10),
	opponentUserID                  VARCHAR(10),
	currentQuestion                 SMALLINT,        
	currentMatchScore               FLOAT,        
	CONSTRAINT fk_MatchSession_userID FOREIGN KEY (userID) REFERENCES Users(userID),
	CONSTRAINT fk_MatchSession_opponentUserID FOREIGN KEY (opponentUserID) REFERENCES Users(userID),
        PRIMARY KEY (matchSessionID)
);

CREATE TABLE imageData
(
        imageID                         VARCHAR(10) PRIMARY KEY,
        question1                       VARCHAR(200),
        question2                       VARCHAR(200),
        question3                       VARCHAR(200),
        question4                       VARCHAR(200),
        answer                          VARCHAR(200)
);  


INSERT INTO users values('foosh','foosh@outlook.com','1@3',0);

INSERT INTO imageData Values('imgID1','','Axum Northern Stelea Park','','','Axum Northern Stelea Park' );

INSERT INTO imageData Values('imgID2','Amphitheatre of El Jem','','','','Amphitheatre of El Jem' );

INSERT INTO imageData values('imgID3','','','','Ruines of Carthage','Ruines of Carthage' );

INSERT INTO imageData values('imgID4','','','','','Volubilis Basilica' );
