package com.kh.bookfinder.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

  private ValidEnum annotation;

  @Override
  public void initialize(ValidEnum constraintAnnotation) {
    this.annotation = constraintAnnotation;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    Object[] enumValues = this.annotation.enumClass().getEnumConstants();
    if (enumValues == null || value == null) {
      return false;
    }
    for (Object enumValue : enumValues) {
      if (value.equals(enumValue.toString())
          || this.annotation.ignoreCase() && value.equalsIgnoreCase(enumValue.toString())) {
        return true;
      }
    }
    return false;
  }
}
