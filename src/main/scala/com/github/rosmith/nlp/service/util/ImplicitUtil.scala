package com.github.rosmith.nlp.service.util

trait ImplicitUtil {

  implicit def convert[T](list: java.util.List[T]): HighOrderFunctionConversion[T] = {
    new HighOrderFunctionConversion(list)
  }

}

object ImplicitUtil extends ImplicitUtil {}