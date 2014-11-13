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
		<restaurant version="1.6">
			<id>praha-u-bucka</id>
			<name>Restaurace U Bůčka</name>
			<phone>(+420) 737 780 745</phone>
			<web>http://www.restauraceubucka.cz/</web>
			<e-mail>info@restauraceubucka.cz</e-mail>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Na Vidouli 1</address>
			<zip>158 00</zip>

			<source time="absolute" firstDayOfWeek="Po" encoding="utf8"
				locale="cs_CZ"
				url="http://www.restauraceubucka.cz/restauraceubucka/3-Denni-nabidka" />

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
			<xsl:apply-templates select="//article[@class='facility-daily-menu']" />
		</restaurant>
	</xsl:template>



	<xsl:template match="article[@class='facility-daily-menu']">
		<menu>
			<xsl:apply-templates select=".//section[@class='daily-menu-for-day']" />
		</menu>
	</xsl:template>

	<xsl:template match="section[@class='daily-menu-for-day']">
		<xsl:variable name="date" select="header/h2/text()" />

		<xsl:for-each select=".//li[starts-with(@class,'daily-menu-item')]">
			<xsl:choose>
				<xsl:when
					test="starts-with(normalize-space(span[@class='name']), 'POLÉVKY ')"></xsl:when>
				<xsl:when
					test="starts-with(normalize-space(span[@class='name']), 'HLAVNÍ JÍDLA')"></xsl:when>
				<xsl:when
					test="starts-with(normalize-space(span[@class='name']), 'SLADKÉ ')"></xsl:when>
				<xsl:when
					test="starts-with(normalize-space(span[@class='name']), 'SALÁTY')"></xsl:when>
				<xsl:when
					test="starts-with(normalize-space(span[@class='name']), 'TĚSTOVINY ')"></xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select=".">
						<xsl:with-param name="date" select="$date" />
						<xsl:with-param name="pos" select="position()" />
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>

	</xsl:template>

	<xsl:template match="li[starts-with(@class,'daily-menu-item')]">
		<xsl:param name="date" />
		<xsl:param name="pos" />
		<meal>
			<xsl:attribute name="dish"><xsl:call-template
				name="dish" /></xsl:attribute>
			<xsl:attribute name="category"><xsl:call-template
				name="category" /></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="$pos" /></xsl:attribute>
			<xsl:attribute name="time"><xsl:value-of select="$date" /></xsl:attribute>
			<xsl:attribute name="ref-time"><xsl:value-of select="$date" /></xsl:attribute>
			<title>
				<xsl:apply-templates select="span[@class='name']//text()" />
			</title>
			<price>
				<xsl:apply-templates select="span[@class='price']//text()" />
			</price>
		</meal>
	</xsl:template>

	<xsl:template name="dish">
		<xsl:choose>
			<xsl:when
				test="../preceding-sibling::header[starts-with(normalize-space(h3), 'HLAVNÍ JÍDLA')]">
				<xsl:text>dinner</xsl:text>
			</xsl:when>
			<xsl:when
				test="../preceding-sibling::header[starts-with(normalize-space(h3), 'POLÉVKY ')]">
				<xsl:text>soup</xsl:text>
			</xsl:when>
			<xsl:when
				test="../preceding-sibling::header[starts-with(normalize-space(h3), 'Menu')]">
				<xsl:text>menu</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>dinner</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="category">
		<xsl:choose>
			<xsl:when
				test="../preceding-sibling::header[starts-with(normalize-space(h3), 'SALÁTY')]">
				<xsl:text>3-salad</xsl:text>
			</xsl:when>
			<xsl:when
				test="../preceding-sibling::header[starts-with(normalize-space(h3), 'SLADKÉ')]">
				<xsl:text>2-sweet</xsl:text>
			</xsl:when>
			<xsl:when
				test="../preceding-sibling::header[starts-with(normalize-space(h3), 'TĚSTOVINY')]">
				<xsl:text>4-pasta</xsl:text>
			</xsl:when>
			<xsl:when
				test="../preceding-sibling::header[starts-with(normalize-space(h3), 'VEGETARIÁNSKÉ ')]">
				<xsl:text>1-vegetarian</xsl:text>
			</xsl:when>
			<xsl:when
				test="(./preceding-sibling::li|.)[starts-with(normalize-space(span[@class='name']), 'SPECIALITA DNE')]">
				<xsl:text>1-live</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>1-normal</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>