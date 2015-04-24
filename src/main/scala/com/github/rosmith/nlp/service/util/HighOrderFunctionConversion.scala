package com.github.rosmith.nlp.service.util

class HighOrderFunctionConversion[T](list: java.util.List[T]) {

  def foreach(func: (T) => Unit) = {
    list.forEach(new java.util.function.Consumer[T]() {
      def accept(syn: T) {
        func.apply(syn)
      }
    })
  }

  def filter(func: (T) => Boolean) = {
    list.stream.filter(new java.util.function.Predicate[T]() {
      def test(syn: T) = {
        func.apply(syn)
      }
    }).collect(java.util.stream.Collectors.toList())
  }

  def map[R](func: (T) => R) = {
    list.stream.map[R](new java.util.function.Function[T, R]() {
      def apply(syn: T): R = {
        func.apply(syn)
      }
    }).collect(java.util.stream.Collectors.toList())
  }

  def limit(l: Long) = {
    list.stream.limit(l).collect(java.util.stream.Collectors.toList())
  }

}