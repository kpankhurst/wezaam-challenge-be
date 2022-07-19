WithdrawalService.java
======================

1. What if payment method does not exist
2. Event fails - current resents the event. What if second attempt fails? We should probably have a fail Repository
3. one withdrawal table for scheduled and asap - asap = now
4. Add retry count to withdrawal table
5. config values for all hard coded values - e.g. loop is every 5 seconds
6. On failure set schedule time to now + x^no_retry where x is configable
7. Add spring logging for failures
8. 