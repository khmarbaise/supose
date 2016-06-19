/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2016 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2016 by Karl Heinz Marbaise
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html If
 * you have any questions about the Software or about the license just write an
 * email to license@soebes.de
 */

package com.soebes.supose.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.soebes.supose.core.FieldNames;

/**
 * This converter will convert the filed names into internal enumeration type
 * {@link FieldNames}.
 *
 * @author Karl Heinz Marbaise
 *
 */
public class FieldNamesConverter implements IStringConverter<FieldNames> {

    @Override
    public FieldNames convert(String value) {
        FieldNames result = null;
        for (FieldNames item : FieldNames.values()) {
            if (item.getValue().equalsIgnoreCase(value)) {
                result = item;
            }
        }
        if (result == null) {
            throw new ParameterException("The given value on command line " + value + " is not a valid field name.");
        }
        return result;
    }
}
