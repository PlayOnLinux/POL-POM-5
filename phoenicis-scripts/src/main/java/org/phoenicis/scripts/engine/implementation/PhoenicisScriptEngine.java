/*
 * Copyright (C) 2015-2017 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.phoenicis.scripts.engine.implementation;

/**
 * A script engine used by Phoenicis
 *
 * @param <R> The internal script result type
 */
public interface PhoenicisScriptEngine<R> {
    /**
     * Sets a global value for the given key (variable) in the script context
     *
     * @param key   The variable name in the script context
     * @param value The corresponding value
     */
    void put(String key, Object value);

    /**
     * Evaluates the given script and returns its result
     *
     * @param script The script
     * @return The result of the evaluated script
     */
    R evaluate(String script);
}
