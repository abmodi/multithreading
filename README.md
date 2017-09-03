# Multithreading

### Thread and Process
* Threads run in a shared memory space while process have a dedicated separate memory space.
* Threads are light weight and generally have very less overhead

A thread has it's own Program Counter, registers, Stack and State while it shares these things with process:
* Address space
* Global variables
* Open files

### Thread creation
There are three ways threads can be created:
* Userspace threads
* Kernel threads
* A combination of two

### States of thread
* NEW: A thread that has just been created and not yet started. new Thread() doesn't create a thread. So it is after thread.start() call.
* RUNNABLE: The thread has been started and is ready to run given CPU cycles
* RUNNING: Thread is executing. In java, there is no explicit RUNNING State and Runnable is only used to refer this
* BLOCKED: Thread is waiting to get lock.
* WAITING: A thread that is waiting without any timeout for another thread to perform a particular action. It may be waiting on IO operations. Thread waits for interrupt to move away from waiting state. A thread can go in waiting state from following operations:
    - wait()
    - join()
    - IO operation
* TIMED_WAITING: A thread that is waiting with specified timeout. 
    - wait(time)
    - join(time)
    - sleep(time)
* TERMINATED: Thread exited normally or abnormally
