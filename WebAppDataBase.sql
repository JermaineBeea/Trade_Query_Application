-- WebAppDataBase Export
-- Generated on: Sun Dec 14 20:24:51 UTC 2025

DROP TABLE IF EXISTS WebAppDataBase;

CREATE TABLE WebAppDataBase (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- Insert data
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('tradeprofit', 0, 0, -306.020485929, -306.020485929);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('profitfactor', 0, 0, 0, 0);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('tradeamount', 10000, 10000, 0, 0);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('openingvalue', 8.251, 8.251, 8.0059999974, 8.0059999974);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('closingvalue', 8.006, 8.006, 8.251000001, 8.251000001);

-- End of export
