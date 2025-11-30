# OS with code and jvm

## 개요
OS의 개념을 자바 코드로 작성하고, JVM에서는 어떻게 추상화/구현되어있는지 학습한다.

## 목적
- OS를 재밌게 공부해보자.
- JVM 수준에서의 동작과 OS 수준에서의 동작을 비교한다.

## 서적: OSTEP
## 목차
| intro                                     | virtualization                                    |                                                      | concurrency                                             | persistence                                                 | 
| ----------------------------------------- | ------------------------------------------------- | ---------------------------------------------------- | ------------------------------------------------------- | ----------------------------------------------------------- | 
| [Preface](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/00-preface.pdf)                 | 3 [Dialogue](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/03-dialogue-virtualization.pdf)      | 12 [Dialogue](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/12-dialogue-vm.pdf)                    | 25 [Dialogue](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/25_dialogue-concurrency.pdf)              | 35 [Dialogue](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/35_dialogue-persistence.pdf)                  | 
| [Preface-Translate](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/00-preface-tx.pdf)    | 4 [Processes](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/04-cpu-intro.pdf)                   | 13 [Address Spaces](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/13-vm-intro.pdf)                 | 26 [Concurrency and Threads](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/26_threads-intro.pdf)      | 36 [I/O Devices](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/36_file-devices.pdf)                       | 
| [TOC](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/00-toc.pdf)                         | 5 [Process API](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/05-cpu-api.pdf)                   | 14 [Memory API](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/14-vm-api.pdf)                       | 27 [Thread API](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/27_threads-api.pdf)                     | 37 [Hard Disk Drives](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/37_file_disks.pdf)                    | 
| 1 [Dialogue](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/01-dialogue-threeeasy.pdf)   | 6 [Direct Execution](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/06-cpu-mechanisms.pdf)       | 15 [Address Translation](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/15-vm-mechanism.pdf)        | 28 [Locks](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/28_threads-locks.pdf)                        | 38 [Redundant Disk Arrays (RAID)](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/38_RAID.pdf)              | 
| 2 [Introduction](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/02-intro.pdf)            | 7 [CPU Scheduling](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/07-cpu-sched.pdf)              | 16 [Segmentation](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/16-vm-segmentation.pdf)            | 29 [Locked Data Structures](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/29_threads-locks-usage.pdf) | 39 [Files and Directories](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/39_interlude-file-directory.pdf) | 
|                                                                   | 8 [Multi-level Feedback](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/08-cpu-sched-mlfq.pdf)   | 17 [Free Space Management](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/17-vm-freespace.pdf)      | 30 [Condition Variables](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/30_threads-cv.pdf)             | 40 [File System Implementation](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/40_FS-implementation.pdf)   | 
|                                                                   | 9 [Lottery Scheduling](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/09-cpu-sched-lottery.pdf)  | 18 [Introduction to Paging](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/18-vm-paging.pdf)        | 31 [Semaphores](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/31_threads-sema.pdf)                    | 41 [Fast File System (FFS)](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/41_FFS.pdf)                     | 
|                                                                   | 10 [Multi-CPU Scheduling](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/10-cpu-sched-multi.pdf) | 19 [Translation Lookaside Buffers](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/19_vm-tlbs.pdf)   | 32 [Concurrency Bugs](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/32_threads-bugs.pdf)              | 42 [FSCK and Journaling](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/42_crash-consistency.pdf)          | 
|                                                                   | 11 [Summary](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/11-cpu-dialogue.pdf)                 | 20 [Advanced Page Tables](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/20_vm-smalltables.pdf)     | 33 [Event-based Concurrency](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/33_threads-events.pdf)     | 43 [Log-Structured File System (LFS)](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/43_LFS.pdf)           |
|                                                                   |                                                                           | 21 [Swapping: Mechanisms](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/21_vm-beyondphys.pdf)      | 34 [Summary](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/34_threads_dialogue.pdf)                   | 44 [Data Integrity and Protection](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/44_data-integrity.pdf)   |
|                                                                   |                                                                           | 22 [Swapping: Policies](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/22_vm-beyondphys-policy.pdf) |                                                                                 | 45 [Summary](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/45_file-dialogue.pdf)                          |
|                                                                   |                                                                           | 23 [Case Study: VAX](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/23_vm-vax.pdf)                  |                                                                                 | 46 [Dialogue](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/46_dialogue-distribution.pdf)                 |
|                                                                   |                                                                           | 24 [Summary](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/24_vm-dialogue.pdf)                     |                                                                                 | 47 [Distributed Systems](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/47_dist-intro.pdf)                 |
|                                                                   |                                                                           |                                                      |                                                                                                         | 48 [Network File System (NFS)](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/48_NFS.pdf)                  |
|                                                                   |                                                                           |                                                      |                                                                                                         | 49 [Andrew File System (AFS)](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/49_AFS.pdf)                   |
|                                                                   |                                                                           |                                                      |                                                                                                         | 50 [Summary](https://pages.cs.wisc.edu/~remzi/OSTEP/Korean/50_dist-dialogue.pdf)                          |

## 객체지향으로 OS 정책/자원 등 이론을 녹여내자.
OS 개념은 대부분 이렇게 쪼갤 수 있다.
- 자원(Resource): CPU, 메모리, 디스크, 락, 페이지 프레임…
- 소비자(Client): 프로세스, 스레드
- 상태(State): 큐, 테이블, 그래프(자원 할당 그래프), 현재 시간…
- 정책/알고리즘(Policy): FCFS vs RR, FIFO vs LRU, Banker's Algorithm…

## 실험으로 증명하자.
이론을 바탕으로 구현했으면, 활용할 수도 있어야 한다.

## OS 구현 코드 - JVM - Java 연결하기
### 1단계: OS 공부
- OS 책/강의로 큰 그림을 잡자.
- “어떤 문제를 해결하려고 쓰이는 개념인지”, 그리고 “목표”와 “How”, 등을 위주로 학습하자.
    - 처음부터 너무 구체적으로 익히려고 하지는 말자.

### 2단계: 중간에서 모델링 (자바 도메인 코드, 미니 시뮬레이터)
이제 바로 밑으로 내려가서, **OS 용어를 자바 클래스로 바꾸는 작업**을 한다.
이론을 그대로 구현하는 것도 좋지만, 문제를 먼저 정의하는 것도 좋다.
- 예: 단일 계좌 잔액에 여러 스레드가 동시에 입출금
    - BankAccount(공유 자원)
    - WorkerThread(스레드 역할)
    - 내가 만든 SimpleMutex, SimpleSemaphore (OS 이론을 반영한 자바 구현)
- “세마포어를 안 쓰면 어떤 버그가 나는지”, “내가 직접 만든 세마포어는 어떤 한계를 갖는지” 등등을 배울 수 있다.

### 3단계: JVM/Java와 연결
JVM에서 이 개념이 어떻게 쓰이거나 추상화되는지 연결해보자.

- 내가 만든 SimpleSemaphore ↔ java.util.concurrent.Semaphore
- 내 뮤텍스 구현 ↔ synchronized, ReentrantLock
- 내가 만든 생산자-소비자 ↔ BlockingQueue 기반 구현

이런 것들을 생각해본다.
- “내 구현은 이 부분에서 깨질 수 있는데, 라이브러리는 이걸 어떻게 보완했을까?”
- “OS 레벨 세마포어/뮤텍스와 Java 레벨의 추상화는 어떤 점이 다를까?”

### 4단계: 마무리 정리
OS 개념으로 다시 올라가서, 흐름을 정리한다.
