-- WebAppDataBase Export
-- Generated on: Sun Dec 14 20:52:45 UTC 2025

DROP TABLE IF EXISTS WebAppDataBase;

CREATE TABLE WebAppDataBase (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- Insert data
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('tradeprofit', -88, -88, -4.970202625, -4.970202625);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('profitfactor', -0.000497, -0.000497, -0.0088, -0.0088);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('tradeamount', 10000, 10000, 177055.155774459, 177055.155774459);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('openingvalue', 17.6967, 17.6967, 17.5496915923, 17.5496915923);
INSERT INTO WebAppDataBase (variable, maximum, minimum, returnmin, returnmax) VALUES ('closingvalue', 17.7055, 17.7055, 17.8538135673, 17.8538135673);

-- End of export
