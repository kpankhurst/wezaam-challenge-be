Issues

1. It only retries once for a failed send. This should retry till it has been success.
   SOLUTION: Set the status to failed or internal error and retry at a fixed time later (incrementing delay time with each retry)
   
2. Retry is immediate
   SOLUTION: Set the retry time to be now + (x * retryCount)
   
3. Does not check if the payment method is valid
   SOLUTION: 
   
4. WithdrawalScheduled and Withdrawal are duplicate of each other, except that one has executeAt. 
   SOLUTION: Store all in WithdrawalScheduled with ASAP set to executeAt as now(). This will also reduce the risk of ASAP executing at the same
   			time as scheduled jobs, and reduce the risk of memory leaks during threading.
   
5. No check on maxwithdrawalamount 
   SOLUTION: further error checking into the push event.
   
6. No messaging service for calls to event send
   SOLUTION: Added

7. No notification system.
   SOLUTION: Added a table with notifications
   
8. JUnit testing
   SOLUTION: Added some rest testing. Others that can be added are event messaging, notifications, retries failures, as well as others.
   
   PLEASE NOTE: no docker file created due to lack of time. Sorry.