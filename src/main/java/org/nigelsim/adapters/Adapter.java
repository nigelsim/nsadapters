/**
 *  This file is part of nsAdapters.
 *
 *  nsAdapters is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  nsAdapters is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with nsAdapters.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nigelsim.adapters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that we adapt for this class.
 * The interfaces we adapt for should be obvious (ie, we implement them).
 * @author nigel
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter {
	Class forClass();
}
