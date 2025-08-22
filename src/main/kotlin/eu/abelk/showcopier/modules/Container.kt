package eu.abelk.showcopier.modules

import kotlin.reflect.KClass

class Container {
    private val creators = mutableMapOf<KClass<out Any>, () -> Any>()
    private val modules = mutableMapOf<KClass<out Any>, Any>()

    inline fun <reified T: Any> register(noinline createModule: () -> T) =
        register(T::class, createModule)

    fun <T: Any> register(type: KClass<T>, createModule: () -> T) {
        if (type in creators) {
            throw IllegalStateException("Module already defined for type ${type.qualifiedName}")
        }
        creators[type] = createModule
    }

    inline fun <reified T: Any> get() =
        get(T::class)

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> get(type: KClass<T>): T =
        if (type in modules) {
            modules[type] as T
        } else {
            invokeCreator(type).also { modules[type] = it }
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Any> invokeCreator(type: KClass<T>) =
        ((creators[type]
            ?: throw IllegalStateException("No module defined for type ${type.qualifiedName}")
        ) as () -> T)()
}
