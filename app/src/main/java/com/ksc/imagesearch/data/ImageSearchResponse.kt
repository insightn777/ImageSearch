package com.ksc.imagesearch.data

data class ImageSearchResponse (
    val documents: List<Document>,
    val meta: Meta
) {
    data class Document(
        val collection: String, // news, blog ...
        val thumbnail_url: String,
        val image_url: String,
        val width: Int,
        val height: Int,
        val display_sitename: String,
        val doc_url: String,
        val datetime: String    // 	문서 작성시간. ISO 8601. [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz]
    )
    data class Meta(
        val total_count: Int,       // 검색어에 검색된 문서수
        val pageable_count: Int,        // total_count 중에 노출가능 문서수
        val is_end: Boolean     // 현재 페이지가 마지막 페이지인지 여부. 값이 false이면 page를 증가시켜 다음 페이지를 요청할 수 있음.
    )
}