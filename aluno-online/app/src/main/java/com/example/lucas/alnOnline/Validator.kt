package com.example.lucas.alnOnline

class Validator {

    fun validateEmail(text: String): Boolean {
        val regex = """^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$""".toRegex()
        return regex.matches(text)
    }

    fun validatePassword(text: String): Boolean {
        val regex = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#${'$'}%!\-_?&])(?=\S+${'$'}).{6,}""".toRegex()
        return !regex.containsMatchIn(text)
    }

    fun validateConfirmPassword(password: String, confirm: String): Boolean {
        return password.equals(confirm)
    }
}
