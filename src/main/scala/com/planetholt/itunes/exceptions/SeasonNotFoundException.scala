package com.planetholt.itunes.exceptions

case class SeasonNotFoundException(show: String, season: Int) extends RuntimeException(s"Found $show, but not season $season")
