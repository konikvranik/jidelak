<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<jidelak>
			<xsl:apply-templates select="/jidelak/config|/html" />
		</jidelak>
	</xsl:template>

	<xsl:template match="/jidelak/config|/html">
		<config>
			<xsl:call-template name="restaurant" />
		</config>
	</xsl:template>

	<xsl:template name="restaurant">
		<restaurant version="1.0">
			<id>praha-gastronom</id>
			<name>Gastronom restaurant &amp; bar</name>
			<phone>(+420) 602 141 629, 251 550 534</phone>
			<e-mail>info@gastronomrestaurant.cz</e-mail>
			<web>http://www.gastronomrestaurant.cz/</web>
			<city>Praha 13</city>
			<country>Česká republika</country>
			<address>Petržílkova 15</address>
			<zip>158 00</zip>

			<source time="absolute" firstDayOfWeek="Po" encoding="utf8"
				dateFormat="E d.M.y" locale="cs_CZ"
				url="http://www.gastronomrestaurant.cz/original-gambrinus-restaurant/poledni-menu/" />

			<open>
				<term day-of-week="Po" from="11:00" to="24:00" />
				<term day-of-week="Út" from="11:00" to="24:00" />
				<term day-of-week="St" from="11:00" to="24:00" />
				<term day-of-week="Čt" from="11:00" to="24:00" />
				<term day-of-week="Pá" from="11:00" to="01:00" />
				<term day-of-week="So" from="12:00" to="01:00" />
				<term day-of-week="Ne" from="12:00" to="23:00" />
				<term to="15:00" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" />
		</restaurant>
	</xsl:template>

	<xsl:template match="div[@class='poledni_menu']">
		<menu>
			<xsl:apply-templates select="h3" />
		</menu>
	</xsl:template>

	<xsl:template match="h3">
		<xsl:apply-templates select="(./following-sibling::table)[1]//tr">
			<xsl:with-param name="date" select="substring-before(.,' svátek')" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="table//tr">
		<xsl:param name="date" />
		<meal>
			<xsl:attribute name="dish">nezratelno</xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
			<xsl:attribute name="time"><xsl:value-of select="$date" />2014</xsl:attribute>
			<title>
				<xsl:value-of select="td[@class='text']" />
			</title>
			<price>
				<xsl:value-of select="td[@class='price']" />
			</price>
		</meal>

	</xsl:template>

</xsl:stylesheet>