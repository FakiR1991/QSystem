/*
 * Copyright (C) 2014 Evgeniy Egorov
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Properties;
import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.exceptions.ServerException;

/**
 *
 * @author Evgeniy Egorov
 */
public class About {

    public static String ver = "";
    public static String date = "";
    public static String db = "";

    public static void load() {
        final Properties settings = new Properties();
        final InputStream inStream = settings.getClass().getResourceAsStream("/ru/apertum/qsystem/common/version.properties");

        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new ServerException("Cant read version. " + ex);
        }
        ver = settings.getProperty(FAbout.VERSION);
        date = settings.getProperty(FAbout.DATE);
        db = settings.getProperty(FAbout.VERSION_DB);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        load();
        System.out.println();
        System.out.println();

        GregorianCalendar gc = new GregorianCalendar();
        int i = (int) (Math.random() * 55) + 4;

        if (gc.get(GregorianCalendar.HOUR_OF_DAY) == 0 || (gc.get(GregorianCalendar.MONTH) == 2 && gc.get(GregorianCalendar.DAY_OF_MONTH) == 23)) {
            i = 6 * (gc.get(GregorianCalendar.SECOND) % 2);
        }
        if (gc.get(GregorianCalendar.HOUR_OF_DAY) == 1 || (gc.get(GregorianCalendar.MONTH) == 4 && gc.get(GregorianCalendar.DAY_OF_MONTH) == 22)) {
            i = (gc.get(GregorianCalendar.SECOND) % 2) + 1;
        }
        if ((gc.get(GregorianCalendar.MONTH) == 10 && gc.get(GregorianCalendar.DAY_OF_MONTH) == 31)) {
            i = 3;
        }
        switch (i) {
            case 0:
                System.out.println();
                System.out.println("0000000000000000000000000000000000000000000000000000000000");
                System.out.println("000000000000000000000__00000000000000000000000000000000000");
                System.out.println("0000000000000000000000___000000000000000000000000000000000");
                System.out.println("0000000000000000000000000_____0000000000000000000000000000");
                System.out.println("00000000000000000000000000000_____000000000000000000000000");
                System.out.println("00000000000000000000000000000000_____000000000000000000000");
                System.out.println("0000000000000000000000000000000000_____0000000000000000000");
                System.out.println("00000000000000__________000000000000_____00000000000000000");
                System.out.println("000000000000___________000000000000000_____000000000000000");
                System.out.println("0000000000___________0000000000000000000____00000000000000");
                System.out.println("000000000__________0000000000000000000000_____000000000000");
                System.out.println("0000000___________000000000000000000000000_____00000000000");
                System.out.println("00000___________00__00000000000000000000000_____0000000000");
                System.out.println("0000___________0_____00000000000000000000000_____000000000");
                System.out.println("000000_______0000______0000000000000000000000____000000000");
                System.out.println("0000000____00000000______00000000000000000000_____00000000");
                System.out.println("000000000000000000000______000000000000000000_____00000000");
                System.out.println("0000000000000000000000______00000000000000000_____00000000");
                System.out.println("000000000000000000000000______000000000000000_____00000000");
                System.out.println("00000000000000000000000000______0000000000000_____00000000");
                System.out.println("000000000000000000000000000_______00000000000_____00000000");
                System.out.println("00000000000000000000000000000______000000000_____000000000");
                System.out.println("0000000000000000000000000000000______000000______000000000");
                System.out.println("000000000000000000__0000000000000______000______0000000000");
                System.out.println("0000000000000000______000000000000_____________00000000000");
                System.out.println("000000000000000_________000000000000__00______000000000000");
                System.out.println("0000000000000_____0_________00000000________00000000000000");
                System.out.println("00000000000____000000_____________________00_0000000000000");
                System.out.println("00000000______0000000000________________0_______0000000000");
                System.out.println("0000000______00000000000000000____00000000______0000000000");
                System.out.println("0000000_____00000000000000000000000000000000___00000000000");
                System.out.println("0000000000000000000000000000000000000000000000000000000000");
                System.out.println("0000000000000000000000000000000000000000000000000000000000");
                System.out.println("0000000000000000000000000000000000000000000000000000000000");
                System.out.println("0000000000000000000000000000000000000000000000000000000000");

                break;
            default:
                printdef();

        }

        System.out.println("");
        printver();
        System.out.println("");
        System.out.println("");
        System.out.println("*** QMS Apertum-QSystem ***");
        System.out.println("   version " + ver);
        System.out.println("   date " + date);
        System.out.println("   DB " + db);
        System.out.println("*** *** *** *** *** *** ***");
    }

    static public void printdef() {
        System.out.println();
        System.out.println("############################################################");
        System.out.println("############################################################");
        System.out.println("###########################      ###########################");
        System.out.println("#########################          #########################");
        System.out.println("#######################              #######################");
        System.out.println("#####################                  @####################");
        System.out.println("###################                      ###################");
        System.out.println("#################         #A#A#            #################");
        System.out.println("###############          @V@@@V@             ###############");
        System.out.println("#############            @@@D@@@               @############");
        System.out.println("###########               @%%%@                  ###########");
        System.out.println("#########                      #@@##@@#            #########");
        System.out.println("#######        #@@@##@@@@# #@@#@#%  #@@#             @######");
        System.out.println("#####            #@@@##@@@@# #@@#    #@#               @####");
        System.out.println("####                        #@@@@#                      ####");
        System.out.println("####                        #@@@@#                      ####");
        System.out.println("######                      #@@@@@#                   @#####");
        System.out.println("########                  %@@@# #@@#                @#######");
        System.out.println("##########               @@@#    #@@#             @#########");
        System.out.println("############             @@#      @@@@@         @###########");
        System.out.println("##############          #@@=        #@@%      @#############");
        System.out.println("################        #@#                 @###############");
        System.out.println("##################                        @#################");
        System.out.println("####################                    @###################");
        System.out.println("######################                @#####################");
        System.out.println("########################            @#######################");
        System.out.println("##########################        ##########################");
        System.out.println("############################    ############################");
        System.out.println("############################################################");
        System.out.println("############################################################");
    }

    static public void printver() {
        System.out.println();
        System.out.println("       $  $          #  ####    #         $  $");
        System.out.println("         $  $      ###    ##  ###       $  $");
        System.out.println("       $$$$$$$      ##   ##    ##      $$$$$$$");
        System.out.println("         $  $       ##  ##     ##       $  $");
        System.out.println("       $  $        #### ##  # ####        $  $");
    }

}
