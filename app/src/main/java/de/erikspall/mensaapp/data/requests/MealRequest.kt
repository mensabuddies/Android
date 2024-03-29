package de.erikspall.mensaapp.data.requests

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import de.erikspall.mensaapp.data.sources.remote.firestore.FirestoreDataSource
import de.erikspall.mensaapp.domain.interfaces.data.Request
import java.time.Duration

class MealRequest(
    override val requestId: String,
    override val expireDuration: Duration = Duration.ofHours(2), //TODO: set duration to next update time of server
    override var source: Source = Source.CACHE,
    private val parameters: MealRequestParameters
) : Request {
    override suspend fun execute(firestoreDataSource: FirestoreDataSource): Result<QuerySnapshot> {
        return firestoreDataSource.fetchMeals(
            source = source,
            foodProviderId = parameters.foodProviderId,
            offset = parameters.offset,
        )
    }
}

data class MealRequestParameters(
    val foodProviderId: Int,
    val offset: Int
)