package com.planetholt.itunes.model

case class ServiceException(msg: String) extends RuntimeException(msg)
