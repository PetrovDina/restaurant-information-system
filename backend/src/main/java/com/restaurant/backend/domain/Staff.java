package com.restaurant.backend.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public abstract class Staff extends User {

    @Digits(integer = 11, fraction = 2)
    @Column(name = "monthly_wage")
    protected BigDecimal monthlyWage;

    @Min(1000)
    @Max(9999)
    @Column(name = "pin")
    protected Integer pin;

    @Override
    public String getUsername() {
        return pin.toString();
    }

    @Override
    public String getPassword() {
        return getUsername();
    }
}
