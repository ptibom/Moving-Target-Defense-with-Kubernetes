/*
 * Moving Target Defense with Kubernetes
 * Copyright (C) 2022  Philip Tibom and Max Buck
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package view;

public class MenuView {
    public void showMenu() {
        System.out.println("Moving Target Defense");
        System.out.println("=====================");
        System.out.println("Menu options");
        System.out.println("1. Start MTD");
        System.out.println("2. Exit");
        System.out.println("Make a selection:");
    }

    public void printLoadBalancerQuestion() {
        System.out.println("\nStart MTD Selected");
        System.out.println("Do you want load balancing? (y/n)");
    }

    public void repeatLoadBalancerQuestion() {
        System.out.println("Invalid input");
        System.out.println("Do you want load balancing? (y/n)");
    }
}
