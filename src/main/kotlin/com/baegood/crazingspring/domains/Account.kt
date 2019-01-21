package com.baegood.crazingspring.domains

import base.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Account(
    email: String,
    password: String,
    name: String
) : BaseEntity() {

    @Column(unique = true, length = 20, nullable = false)
    var email = email
        private set

    @Column(nullable = false)
    var password = password
        private set

    @Column(length = 20, nullable = false)
    var name = name
        private set
}
