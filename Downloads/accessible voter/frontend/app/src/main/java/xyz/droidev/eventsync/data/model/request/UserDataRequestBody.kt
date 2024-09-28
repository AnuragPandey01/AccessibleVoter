package xyz.droidev.eventsync.data.model.request


data class UserData(
    val aadhaar: String,
    val full_name: String,
    val phone: String,
    val bounds: List<Float>,
    val face_points: List<FaceMeshPoint>,
    val triangle_indices: List<List<Int>>? = null
)

data class FaceMeshPoint(
    val index: Int,
    val position: List<Float>
)