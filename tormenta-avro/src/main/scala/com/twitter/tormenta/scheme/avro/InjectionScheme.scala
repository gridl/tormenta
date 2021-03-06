/*
 * Copyright 2013 Twitter inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.twitter.tormenta.scheme.avro

import com.twitter.bijection.Injection
import scala.util.{ Failure, Success }
import com.twitter.tormenta.scheme.Scheme
import java.nio.ByteBuffer

object InjectionScheme {
  def apply[T](inj: Injection[T, Array[Byte]]): InjectionScheme[T] =
    new InjectionScheme[T](inj)
}

class InjectionScheme[T](inj: Injection[T, Array[Byte]]) extends Scheme[T] {
  override def decode(buf: ByteBuffer): TraversableOnce[T] = {
    val bytes = Array.ofDim[Byte](buf.remaining())
    buf.get(bytes)
    inj.invert(bytes) match {
      case Success(x) => Seq(x)
      case Failure(x) => throw x
    }
  }
}
