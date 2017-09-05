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

### Types of threads in Java
* User Thread: JVM will not exit until all user threads finish their execution. Main thread is a user thread. All threads are by default user threads
* Daemon Thread: These threads run in background and JVM doesn't wait for daemon threads to finish their tasks. A thread can be explicitly made daemon thread by `setDaemon(true)`. Daemon thread property is inherited from parent thread.

### Java Volatile
Volatile keyword is used to make sure changes done by a thread is visible to another thread. By default, due to optimizations, read and write of a variable can happen on CPU cache. Since each thread might be running on different CPU, they might be updating their own cache instead of main memory. The `volatile` keyword is being used to mark a variable as `being stored in main memory`. This ensures read and write of that variable happens from/to main memory.

### Java Synchronized Blocks
Synchronized blocks in java are used to avoid race conditions. Synchronized keyword can be used to mark four different kinds of blocks:
* Instance methods: Synchronized on `this` ie. instance of the Object
* Static methods: Synchronized on class object ie. MyClass.class
* Code blocks inside instance methods: Generally synchronized on `this`.
* Code blocks inside class methods: Generally synchronized on class object.

Only one thread can execute instructions written inside synchronized block at a time. All the variables updated/read inside synchronized variable behave as volatile.

### Thread Local
ThreadLocal class in java enables to create variables that can only be read and written by same thread. Thus, even if two threads are executing the same code and the code has a reference to ThreadLocal variable, then two threads can't see each other's `ThreadLocal` variable.

### Thread Signalling
The purpose of thread signalling is to enable threads to send signals to each other. Additionally, thread signalling allows threads to wait for signals from other threads. 

#### Signaling via Shared Object
Create a shared object and all threads share that object. The state of that object is being used to signal threads.
```java
public class MySignal {

  protected boolean hasDataToProcess = false;

  public synchronized boolean hasDataToProcess(){
    return this.hasDataToProcess;
  }

  public synchronized void setHasDataToProcess(boolean hasData){
    this.hasDataToProcess = hasData;  
  }
}
```

####Busy Wait
One thread keeps waiting for singal from another thread before starting any processing. In this case, CPU will go quite high.

```java
protected MySignal sharedSignal = ...;

while(!sharedSignal.hasDataToProcess()) {
    // do nothing ... busy wait
}
```


####wait(), notify() and notifyAll()
Java Object provides these three functions and these functions are available with every object.

1. wait(): wait() on an object can only be called, if lock has been taken on that object. On calling wait(), it releases the lock on that object and thread moves into waiting state. Thread will move out of waiting state only if some thread calls notify on it. On being activated again, it needs to take the lock again and then execution will start from that place.
2. notify(): Similar to wait(), notify can only be called on the object, if lock has been taken on that object. Notify() notifies one of the thread waiting on that object. Notify doesn't release the lock, hence the thread waiting will only start executing after notify block completes. Notify won't have any impact, if no thread was waiting on that object at that point of time.
3. notifyAll(): Notifies all the threads waiting, but only one of them can proceed because only one of them will be able to take lock on that object.

```java
public class MyWaitNotify{

  Object myMonitorObject = new Object();

  public void doWait(){
    synchronized(myMonitorObject){
      try{
        myMonitorObject.wait();
      } catch(InterruptedException e){...}
    }
  }

  public void doNotify(){
    synchronized(myMonitorObject){
      myMonitorObject.notify();
    }
  }
}
```

Waiting thread will call doWait(), and the notifying thread will call doNotify().

####Missed Signals
As mentioned above, notify() won't do anything if no thread is waiting on that object. If thread that is going to wait on that object starts after notify signal has been dispatched, that thread will never be able to wake up again and program will never end. To avoid losing signals, we need to store them inside the class.

```java
public class MyWaitNotify2{

  Object myMonitorObject = new Object();
  boolean wasSignalled = false;

  public void doWait(){
    synchronized(myMonitorObject){
      if(!wasSignalled){
        try{
          myMonitorObject.wait();
         } catch(InterruptedException e){...}
      }
      //clear signal and continue running.
      wasSignalled = false;
    }
  }

  public void doNotify(){
    synchronized(myMonitorObject){
      wasSignalled = true;
      myMonitorObject.notify();
    }
  }
}
```

####Spurious Wakeups
For inexplicable reasons, it is possible for threads to wake up even if notify() and notifyAll() has not been called. This is called Spurious wakeups. To guard against spurious wakeups the signal variable is checked inside a while loop instead of if statement. Such a while loop is called spin lock. 

```java
public class MyWaitNotify3{

  MonitorObject myMonitorObject = new MonitorObject();
  boolean wasSignalled = false;

  public void doWait(){
    synchronized(myMonitorObject){
      while(!wasSignalled){
        try{
          myMonitorObject.wait();
         } catch(InterruptedException e){...}
      }
      //clear signal and continue running.
      wasSignalled = false;
    }
  }

  public void doNotify(){
    synchronized(myMonitorObject){
      wasSignalled = true;
      myMonitorObject.notify();
    }
  }
}
```

###Deadlock
```
Thread 1  locks A, waits for B
Thread 2  locks B, waits for A
```

###Deadlock Prevention
1. Lock Ordering - Deadlock occurs if multiple threads need the same lock but obtains them in different order. If all thread, obtains lock in order of A, B, C, deadlock will never occur.
2. Lock Timeout - In case a lock is not obtained in some time interval, thread unlocks already taken locks and sleeps randomly for some amount of time and then retry again.
3. Deadlock Detection - Every thread before taking and releasing lock logs it and through logs deadlock can be detected.

###Livelock
As with deadlock, livelocked threads are unable to make further progress. However, the threads are not blocked — they are simply too busy responding to each other to resume work. This is comparable to two people attempting to pass each other in a corridor: Alphonse moves to his left to let Gaston pass, while Gaston moves to his right to let Alphonse pass. Seeing that they are still blocking each other, Alphone moves to his right, while Gaston moves to his left. They're still blocking each other, so...

One good example is Single Spoon problem, where a couple goes for dinner but only one spoon is available. Both of them are loving to each other and passes the spoon to another person and causing livelock.

###Nested Monitor Lockout
```
Thread 1 synchronizes on A
Thread 1 synchronizes on B (while synchronized on A)
Thread 1 decides to wait for a signal from another thread before continuing
Thread 1 calls B.wait() thereby releasing the lock on B, but not A.

Thread 2 needs to lock both A and B (in that sequence)
        to send Thread 1 the signal.
Thread 2 cannot lock A, since Thread 1 still holds the lock on A.
Thread 2 remain blocked indefinately waiting for Thread1
        to release the lock on A

Thread 1 remain blocked indefinately waiting for the signal from
        Thread 2, thereby
        never releasing the lock on A, that must be released to make
        it possible for Thread 2 to send the signal to Thread 1, etc.
```

This results in similar situation as of deadlock. Though it is different as in this case, locks are taken in same order, but still threads end up getting stuck.


