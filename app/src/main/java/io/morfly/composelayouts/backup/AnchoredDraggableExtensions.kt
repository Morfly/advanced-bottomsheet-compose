//package io.morfly.composelayouts.backup
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.gestures.AnchoredDraggableState
//import androidx.compose.foundation.gestures.DraggableAnchors
//import androidx.compose.foundation.gestures.animateTo
//import kotlin.reflect.KMutableProperty
//import kotlin.reflect.full.declaredMemberProperties
//import kotlin.reflect.full.memberProperties
//import kotlin.reflect.jvm.javaSetter
//
//@OptIn(ExperimentalFoundationApi::class)
//suspend fun <T> AnchoredDraggableState<T>.updateAnchorsAnimated(
//    newAnchors: DraggableAnchors<T>,
//    newTarget: T = if (!offset.isNaN()) {
//        newAnchors.closestAnchor(offset) ?: targetValue
//    } else targetValue
//) {
//    if (anchors != newAnchors) {
//        if (trySetAnchors(newAnchors)) {
//            println("TTAGG true")
////            animateTo(newTarget)
//        } else {
//            println("TTAGG false")
////            updateAnchors(newAnchors, newTarget)
//        }
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//fun <T> AnchoredDraggableState<T>.trySetAnchors1(anchors: DraggableAnchors<T>): Boolean {
//    val propertyName = AnchoredDraggableState<*>::anchors.name
//
//    @Suppress("UNCHECKED_CAST")
//    val property = AnchoredDraggableState::class.declaredMemberProperties
//        .firstOrNull { it.name == propertyName } as? KMutableProperty<DraggableAnchors<T>>
//        ?: return false
//
//    val setter = property.setter
//    setter.visibility
//
//    try {
//        setter.call(this, anchors)
//    } catch (t: Throwable) {
//        println("TTAGG t: $t")
//        return false
//    }
//    return true
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//fun <T> AnchoredDraggableState<T>.trySetAnchors2(anchors: DraggableAnchors<T>): Boolean {
//    val propertyName = this::anchors.name
//
//    @Suppress("UNCHECKED_CAST")
//    val property = this::class.declaredMemberProperties
//        .firstOrNull { it.name == propertyName } as? KMutableProperty<DraggableAnchors<T>>
//        ?: return false
//
//    val setterName = property.javaSetter?.name ?: return false
//
//    val field = this.javaClass.declaredFields
//    println("TTAGG field: ${field.map { it.name }}")
//
//    try {
//        val method = this.javaClass.getDeclaredMethod("setAnchors")
//        println("TTAGG method: $method")
//        method.isAccessible = true
//        method.invoke(this, anchors)
//    } catch (t: Throwable) {
//        println("TTAGG error: $t")
//        return false
//    }
//
//    println("TTAGG success")
//    return true
//
////    this.javaClass.declaredFields
////    println("TTAGG members: ${this.javaClass.declaredMethods.map { it.name }}")
////    println("TTAGG fields: ${this.javaClass.declaredFields.map { it.name }}")
//////    val property = this.javaClass.getDeclaredMethod("setAnchors")
////    property.isAccessible = true
////    try {
//////        property(this, anchors)
////        property.invoke(this, anchors)
////    } catch (t: Throwable) {
////        println("TTAGG t: $t")
////        return false
////    }
////    return true
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//fun <T> AnchoredDraggableState<T>.trySetAnchors(anchors: DraggableAnchors<T>): Boolean {
//    val property = this::class.memberProperties.find { it.name == "anchors" }
//    if (property is KMutableProperty<*>) {
//        println("TTAGG prop: ${property?.name}")
//        property.setter.call(this, anchors)
//    }
//    return true
//
//}