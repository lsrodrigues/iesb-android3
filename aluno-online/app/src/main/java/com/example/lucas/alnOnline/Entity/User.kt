package com.example.lucas.alnOnline.Entity

class User {
    var id: String = ""
    var name: String = ""
    var mail: String = ""
    var password: String = ""
    var enrollment: String = ""
    var phone: String = ""
    var image: String = ""

    constructor() {}

    constructor(mail: String, password: String) {
        this.mail = mail
        this.password = password
    }

}