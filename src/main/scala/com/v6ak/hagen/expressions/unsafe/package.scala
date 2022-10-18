package com.v6ak.hagen.expressions

/**
 * Expressions that should be used with care.
 *
 * The included expressions satisfy two rules:
 *
 * (1) They allow you to force wrong type.
 * (2) They aren't intended to be used directly.
 * 
 * There are few classes (e.g., Entity) that satisfy (1), but not (2). We don't want to make using unsafe to be a daily
 * job. This might change for some implementations of Entity when automatic imports are implemented.
 */

package object unsafe {

}