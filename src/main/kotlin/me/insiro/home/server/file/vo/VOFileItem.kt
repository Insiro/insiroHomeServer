package me.insiro.home.server.file.vo

data class VOFileItem(
    override val domain: String,
    override val collection: String,
    override val name: String,
) : IFileItem {
    constructor(collection: IFileCollection, fileName:String): this(
        collection.domain, collection.collection, fileName
    )
}