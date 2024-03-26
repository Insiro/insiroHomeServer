package me.insiro.home.server.file.vo

data class VOFileCollection(
    override val domain: String,
    override val collection: String,
) : IFileCollection {
    constructor(collection: IFileCollection) : this(collection.domain, collection.collection)
}