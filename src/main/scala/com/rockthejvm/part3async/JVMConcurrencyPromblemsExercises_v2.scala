package com.rockthejvm.part3async

object JVMConcurrencyPromblemsExercises_v2 {

  //1

  def inceptionThreads_solution(maxThreads: Int, i: Int = 1): Thread = {
    new Thread(() => {
      if (i < maxThreads) {
        val newThread = inceptionThreads_solution(maxThreads, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from thread $i")
    })
  }

  def main(args: Array[String]): Unit = {
    inceptionThreads_solution(5).start()
  }
}
