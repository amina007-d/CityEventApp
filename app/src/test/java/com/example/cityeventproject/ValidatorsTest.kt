package com.example.cityeventproject

import com.example.cityeventproject.domain.logic.Validators
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidatorsTest {
    @Test fun email_validation() {
        assertThat(Validators.isValidEmail("a@b.com")).isTrue()
        assertThat(Validators.isValidEmail("bad_email")).isFalse()
    }

    @Test fun password_validation_min_len() {
        assertThat(Validators.validatePassword("12a")).isNotNull()
    }

    @Test fun password_validation_requires_number() {
        assertThat(Validators.validatePassword("abcdef")).isNotNull()
        assertThat(Validators.validatePassword("abcde1")).isNull()
    }

    @Test fun comment_validation_bounds() {
        assertThat(Validators.validateComment("")).isNotNull()
        assertThat(Validators.validateComment("hi")).isNotNull()
        assertThat(Validators.validateComment("hello")).isNull()
        assertThat(Validators.validateComment("a".repeat(281))).isNotNull()
    }
}
