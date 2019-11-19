package com.company

sealed trait BaseRecord

case class Name(name: String) extends BaseRecord()
case class Icon(icon: String) extends BaseRecord()

case class Key(key: String)
case class Value(payload: Seq[BaseRecord])
