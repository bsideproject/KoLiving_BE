package com.koliving.api.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class CustomNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        String upperCaseName = name.getText().toUpperCase();
        return Identifier.toIdentifier(upperCaseName);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        String upperSnakeCaseName = name.getText()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
        return Identifier.toIdentifier(upperSnakeCaseName);
    }
}
