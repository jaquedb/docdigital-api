package com.docdigital.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class DocdigitalApplication

fun main(args: Array<String>) {
	runApplication<DocdigitalApplication>(*args)
}

