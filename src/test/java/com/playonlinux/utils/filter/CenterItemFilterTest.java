/*
 * Copyright (C) 2015 Kaspar Tint
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

package com.playonlinux.utils.filter;

import com.playonlinux.dto.ui.AppsItemDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

public class CenterItemFilterTest {

    private MockFilterObserver mockFilterObserver;

    @Before
    public void setUp() {
        mockFilterObserver = new MockFilterObserver();
    }

    @Test
    public void testFilterWithString() {
        mockFilterObserver.setTitle("7-");
        assertEquals(1, mockFilterObserver.getFilteredCenterItems().size());

        /**
          * We show regular applications even when grouping is applied.
          */
        mockFilterObserver.setTitle("7-z");
        mockFilterObserver.setShowCommercial(true);
        mockFilterObserver.setShowNoCD(true);
        assertEquals(1, mockFilterObserver.getFilteredCenterItems().size());
    }

    @Test
    public void testFilterWithStringAndGroupings() {
        mockFilterObserver.setTitle("Dia");
        mockFilterObserver.setShowCommercial(true);
        mockFilterObserver.setShowNoCD(true);
        assertEquals(1, mockFilterObserver.getFilteredCenterItems().size());

        /**
         * We only show commercial and CD requiring applications when grouping is applied
         */
        mockFilterObserver.setTitle("Dia");
        mockFilterObserver.setShowCommercial(false);
        mockFilterObserver.setShowNoCD(false);
        assertEquals(0, mockFilterObserver.getFilteredCenterItems().size());
    }

    @Test
    public void testFilterWithEmptyString() {
        mockFilterObserver.setTitle("");
        assertEquals(0, mockFilterObserver.getFilteredCenterItems().size());
    }

    @Test
    public void testFilterWithWrongString() {
        mockFilterObserver.setTitle("WRONG");
        assertEquals(0, mockFilterObserver.getFilteredCenterItems().size());
    }

    public class MockFilterObserver implements Observer {

        private final CenterItemFilter filter = new CenterItemFilter();
        private final List<AppsItemDTO> appsItemDTOs;
        private List<AppsItemDTO> filteredAppsItemDTOs;

        MockFilterObserver() {
            appsItemDTOs = new ArrayList<>();
            appsItemDTOs.add(new AppsItemDTO.Builder()
                    .withName("7-zip")
                    .withCategoryName("Accessories")
                    .withDescription("")
                    .withRequiresNoCd(false)
                    .withTesting(false)
                    .withCommercial(false)
                    .build());
            appsItemDTOs.add(new AppsItemDTO.Builder()
                    .withName("Diablo II")
                    .withCategoryName("Games")
                    .withDescription("")
                    .withRequiresNoCd(true)
                    .withTesting(false)
                    .withCommercial(true)
                    .build());
            filter.addObserver(this);
        }

        @Override
        public void update(Observable observable, Object o) {
            filteredAppsItemDTOs = appsItemDTOs.stream().filter(filter::apply).collect(Collectors.toList());
        }

        public void setTitle(String title) {
            filter.setTitle(title);
        }

        public void setShowNoCD(boolean showNoCD) {
            filter.setShowNoCd(true);
        }

        public void setShowCommercial(boolean showCommercial) {
            filter.setShowCommercial(showCommercial);
        }

        public List<AppsItemDTO> getFilteredCenterItems() {
            return this.filteredAppsItemDTOs;
        }
    }
}
