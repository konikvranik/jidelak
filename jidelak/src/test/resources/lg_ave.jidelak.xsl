<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<jidelak>
			<xsl:apply-templates select="/config|/html" />
		</jidelak>
	</xsl:template>

	<xsl:template match="/config">
		<config>
			<id>praha-lunch-garden-avenir</id>
			<name>Lunch Garden Avenir</name>
			<encoding>cp1250</encoding>

			<!-- relative|absolute -->
			<!-- day|week|month|year -->

			<source time="relative" base="week" firstDayOfWeek="mo"
				timeOffset="0"
				url="http://lgavenir.cateringmelodie.cz/cz/denni-menu-tisk.php" />

			<source time="relative" base="week" firstDayOfWeek="mo"
				timeOffset="1"
				url="http://lgavenir.cateringmelodie.cz/cz/denni-menu-pristi-tyden-tisk.php" />

		</config>

	</xsl:template>

	<xsl:template match="/html">
		<restaurant>
			<name>Food Garden Kavčí hory</name>
			<phone>+420 731 401 714</phone>
			<e-mail>pavel.pechaty.ml@cateringmelodie.cz</e-mail>
			<web>http://fgkavcihory.cateringmelodie.cz/cz/</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Radlická 714/113a</address>
			<zip>158 00</zip>
			<open>
				<term date="1.1.2010" closed="true" />
				<term day-of-week="mo" from="8:00" to="17:00" />
				<term day-of-week="tu" from="8:00" to="17:00" />
				<term day-of-week="we" from="8:00" to="17:00" />
				<term day-of-week="th" from="8:00" to="17:00" />
				<term day-of-week="fr" from="8:00" to="17:00" />
			</open>
		</restaurant>
		<menu>
			<xsl:apply-templates
				select="//table[@class='tb_jidelak' and position() = 1]/tbody/tr[1]/td"
				mode="days" />
		</menu>
	</xsl:template>

	<xsl:template match="td" mode="days">
		<xsl:variable name="pos" select="position()" />
		<xsl:variable name="ref-time"
			select="substring-before(/html/body/div[@class='okno-tisk']/center/div[@class='vnitrek-tisk']/p[@class='centrovani velka']/b/text(), ' ') " />

		<xsl:apply-templates
			select="../../tr[2]/td[$pos]/p[normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'normal'" />
			<xsl:with-param name="dish" select="'soup'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../tr[position() &gt; 2]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'normal'" />
			<xsl:with-param name="dish" select="'lunch'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and ( position() = 1)]/tbody/tr[2]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'superior'" />
			<xsl:with-param name="dish" select="'lunch'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and (position() = 2 )]/tbody/tr[2]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'live'" />
			<xsl:with-param name="dish" select="'lunch'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and position() = 3]/tbody/tr[2]/td[normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'pasta'" />
			<xsl:with-param name="dish" select="'lunch'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="td|td/p">
		<xsl:param name="type" />
		<xsl:param name="dish" />
		<xsl:param name="time" />
		<xsl:param name="ref-time" />
		<meal order="{position()}" category="{$type}" dish="{$dish}"
			time="{$time}" ref-time="{$ref-time}">

			<xsl:value-of select="." />
		</meal>
	</xsl:template>

</xsl:stylesheet>