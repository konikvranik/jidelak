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
			<id>praha-na_kopci</id>
			<name>RESTAURACE &amp; TERASA NA KOPCI</name>
			<phone>+420 251 553 102 </phone>
			<e-mail>info@nakopci.com</e-mail>
			<web>http://www.nakopci.com/</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address> K Závěrce 2774/20</address>
			<zip>150 00</zip>
		
			<source time="absolute" firstDayOfWeek="Po" encoding="utf8"
				locale="cs_CZ" url="http://www.nakopci.com/menu-restaurace-na-kopci.html" />
			<open>
				<term day-of-week="Po" from="11:30" to="14:30" />
				<term day-of-week="Po" from="17:30" to="23:00" />
				<term day-of-week="Út" from="11:30" to="14:30" />
				<term day-of-week="Út" from="17:30" to="23:00" />
				<term day-of-week="St" from="11:30" to="14:30" />
				<term day-of-week="St" from="17:30" to="23:00" />
				<term day-of-week="Čt" from="11:30" to="14:30" />
				<term day-of-week="Čt" from="17:30" to="23:00" />
				<term day-of-week="Pá" from="11:30" to="14:30" />
				<term day-of-week="Pá" from="17:30" to="23:00" />
				<term day-of-week="So" from="11:30" to="23:00" />
				<term day-of-week="Ne" from="11:30" to="23:00" />
				<term to="15:00" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//*[@id='mainContainer']" />
		</restaurant>
	</xsl:template>

	<xsl:template match="*[@id='mainContainer']">
		<menu>
			<xsl:apply-templates select="*" />
		</menu>
	</xsl:template>

	<xsl:template match="*[@id='mainContainer']/div/div[starts-with(@class,'menuItem')]">
		<meal>
			<xsl:apply-templates select="div[starts-with(@class, 'menuSubsection')]" mode="dish"/>
			<title>
				<xsl:apply-templates select="div[starts-with(@class, 'menuSubsection')]"/>
			</title>
			<description>
				<xsl:apply-templates select="div[starts-with(@class, 'menuName')]"/>
			</description>
			<price>
				<xsl:apply-templates select="div[starts-with(@class, 'menuPrice')]"/>
			</price>
							
		</meal>
	</xsl:template>

	<xsl:template match="div[starts-with(@class, 'menuSubsection')]" mode="dish">
		<xsl:attribute name="dish"><xsl:choose>
		<xsl:when test="starts-with(.,'POLÉVKA')">soup</xsl:when>
		<xsl:when test="starts-with(.,'HLAVNÍ JÍDLO')">dinner</xsl:when>
		</xsl:choose>dinner</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>