# agh-mwo-enroller-rest
RESTful back-end service for Enroller app

## Paths
| Path | Resource |
| ------------- | --------------- |
|`/participants`| all participants|
|`/participants/{login}`| specific participant|
|`/meetings`| all meetings|
|`/meetings/{id}`| specific meeting|
|`/meetings/{id}/participants`| meeting's participans|
|`/meetings/sort/{attribute}`| all meetings sorted by `attribute`|
|`/meetings/search/titledesc/{phrase}`| return meetings with matching `phrase` in title or description|
|`/meetings/search/participants/{login}`| returns all meetings in which participant `login` takes part|

## Live example
Heroku [mwo-enroller-rest.herokuapp.com](https://mwo-enroller-rest.herokuapp.com)
