package com.docdigital.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DocdigitalApplication

fun main(args: Array<String>) {
	runApplication<DocdigitalApplication>(*args)
}
