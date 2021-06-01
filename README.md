# qapital-backend-test by Micke Björklund

## Comments

I implemented the missing business logic in the new "domain class" SavingsRule, but kept the original SavingsRule class 
as a pure JSON serializer/deserializer (SavingsRuleJson) to leave the API intact.
(In the name of consequence, the SavingsEvent class should have been divided into two classes similarly, but I skipped that.)

Multiple occurrences of the same savings goal id in SavingsRule.savingsGoalIds are ignored and treated as a single occurrence.

The full savings amount is divided across all savings goals. Meaning that, when the savings amount not can be divided equally
across all savings goals, one of the savings events will receive a deviating amount.

The task specification says about the Roundup rule: 
"...it rounds the amount on the transaction to the nearest multiple of the configured roundup amount..."
I have chosen to interpret thi as the nearest _upper_ multiple, since the rule is named Round*up*.

I added the SavingsEventService as a SavingsEvent producer.

Some unit tests where added for the new SavingsRule class.

All newly introduced classes are written in Kotlin.

## On the endpoints

I found the REST endpoint naming to be a bit hard to understand.
It seems odd to use a path parameter for a user, {userId}, in a path not relating to a user.
This would be more conventional option in my opinion:

`/api/savings-rules?active={boolean}&user={userId}`

My overall guiding rule however, was to not mess with the existing API, so I simply added the new endpoint within
the existing structure:

`POST /api/savings/rule/executions`

## New dependencies

* `kotlin-stdlib` and `kotlin-reflect` to use Kotlin
* `jackson-datatype-jsr310` for ISO 8601 formatting
* `spring-boot-starter-test` for tests
* `assertj-core` for tests
