package me.insiro.home.server.file.vo

data class VOTextFileItem(
    override val domain: String,
    override val collection: String,
    override val name: String,
    val content: String? = null,
) : IFileItem {
    constructor(collection: IFileCollection, fileName: String, content: String? = null) : this(
        collection.domain, collection.collection, fileName, content
    )
}