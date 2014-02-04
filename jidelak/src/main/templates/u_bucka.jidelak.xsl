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
		<restaurant version="1.4">
			<id>praha-u-bucka</id>
			<name>Restaurace U Bůčka</name>
			<phone>(+420) 737 780 745, 737 711 968</phone>
			<web>http://www.restauraceubucka.cz/</web>
			<e-mail>info@restauraceubucka.cz</e-mail>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Mezi Lány 15</address>
			<zip>158 00</zip>

			<source time="absolute" firstDayOfWeek="Po" encoding="utf8"
				locale="cs_CZ" url="http://www.restauraceubucka.cz/restauraceubucka/3-Denni-nabidka" />

			<open>
				<term day-of-week="Po" from="10:30" to="23:00" />
				<term day-of-week="Út" from="10:30" to="23:00" />
				<term day-of-week="St" from="10:30" to="23:00" />
				<term day-of-week="Čt" from="10:30" to="23:00" />
				<term day-of-week="Pá" from="10:30" to="23:00" />
				<term day-of-week="So" from="11:00" to="23:00" />
				<term day-of-week="Ne" from="11:00" to="22:00" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//*[@id='incenterpage']/table" />
		</restaurant>
	</xsl:template>



	<xsl:template match="*[@id='incenterpage']/table">
		<menu>
			<xsl:apply-templates select="//tr[td[1]/text() and td[last()]/text()]">
			</xsl:apply-templates>
		</menu>
	</xsl:template>

	<xsl:template match="tr">
			<meal>
				<xsl:attribute name="dish"><xsl:choose>
			<xsl:when
					test="./preceding-sibling::tr[starts-with(td/.,  'HLAVNÍ JÍDLA')]">dinner</xsl:when>
		<xsl:otherwise>soup</xsl:otherwise>
		</xsl:choose></xsl:attribute>
				<xsl:attribute name="category"><xsl:choose>
			<xsl:when test="./preceding-sibling::tr[starts-with(td/.,  'STEAKY')]">5-steak</xsl:when>
			<xsl:when test="./preceding-sibling::tr[starts-with(td/.,  'TĚSTOVINY')]">4-pasta</xsl:when>
			<xsl:when
					test="./preceding-sibling::tr[starts-with(td/.,  'JÍDLA PRO DÁMY')]">3-ladies</xsl:when>
			<xsl:when test="./preceding-sibling::tr[starts-with(td/.,  'SALÁTY ')]">2-salad</xsl:when>
		<xsl:otherwise>1-normal</xsl:otherwise>
		</xsl:choose></xsl:attribute>
				<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
				<title>
					<xsl:apply-templates select="preceding-sibling::tr[1]" mode="multiline"/>
					<xsl:value-of select="td[1]" />
				</title>
				<price>
					<xsl:value-of select="td[last()]" />
				</price>
			</meal>

	</xsl:template>
	
	<xsl:template match="tr" mode="multiline">
	<xsl:if test="td[1]/text() and (count(td[last()]/text()) = 0)">
		<xsl:choose>
			<xsl:when
					test="starts-with(td/.,  'POLÉVKY')"></xsl:when>
			<xsl:when
					test="starts-with(td/.,  'HLAVNÍ JÍDLA')"></xsl:when>
			<xsl:when test="starts-with(td/.,  'STEAKY')"></xsl:when>
			<xsl:when test="starts-with(td/.,  'TĚSTOVINY')"></xsl:when>
			<xsl:when
					test="starts-with(td/.,  'JÍDLA PRO DÁMY')"></xsl:when>
			<xsl:when test="starts-with(td/.,  'SALÁTY ')"></xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="preceding-sibling::tr[1]" mode="multiline"/>
			<xsl:value-of select="td[1]" />
			<xsl:text> </xsl:text>
		</xsl:otherwise>
		</xsl:choose>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>